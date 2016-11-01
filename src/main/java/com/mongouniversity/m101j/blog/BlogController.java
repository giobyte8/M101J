/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongouniversity.m101j.blog;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.setPort;

/**
 * This class encapsulates the controllers for the blog web application.  It delegates all interaction with MongoDB
 * to three Data Access Objects (DAOs).
 * <p/>
 * It is also the entry point into the web application.
 */
public class BlogController {
    private final Configuration cfg;
    private final BlogPostDAO blogPostDAO;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new BlogController("mongodb://localhost");
        }
        else {
            new BlogController(args[0]);
        }
    }

    private BlogController(String mongoURIString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final MongoDatabase blogDatabase = mongoClient.getDatabase("blog");

        blogPostDAO = new BlogPostDAO(blogDatabase);
        userDAO = new UserDAO(blogDatabase);
        sessionDAO = new SessionDAO(blogDatabase);

        cfg = createFreemarkerConfiguration();
        setPort(8082);
        initializeRoutes();
    }

    private void initializeRoutes() throws IOException {

        // this is the blog home page
        Spark.get("/", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                String username = sessionDAO.findUserNameBySessionId(getSessionCookie(request));

                List<Document> posts = blogPostDAO.findByDateDescending(10);
                SimpleHash root = new SimpleHash();

                root.put("myposts", posts);
                if (username != null) {
                    root.put("username", username);
                }

                StringWriter writer = new StringWriter();
                cfg.getTemplate("blog_template.ftl").process(root, writer);
                return writer;
            }
        });

        // used to display actual blog post detail page
        Spark.get("/post/:permalink", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                String permalink = request.params(":permalink");

                System.out.println("/post: get " + permalink);

                Document post = blogPostDAO.findByPermalink(permalink);
                if (post == null) {
                    response.redirect("/post_not_found");
                }
                else {
                    // empty comment to hold new comment in form at bottom of blog entry detail page
                    SimpleHash newComment = new SimpleHash();
                    newComment.put("name", "");
                    newComment.put("email", "");
                    newComment.put("body", "");

                    SimpleHash root = new SimpleHash();

                    root.put("post", post);
                    root.put("comments", newComment);


                    cfg.getTemplate("entry_template.ftl").process(root, writer);
                }

                return writer;
            }
        });

        // handle the signup post
        Spark.post("/signup", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                String email = request.queryParams("email");
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                String verify = request.queryParams("verify");

                HashMap<String, String> root = new HashMap<String, String>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                root.put("email", StringEscapeUtils.escapeHtml4(email));

                if (validateSignup(username, password, verify, email, root)) {
                    // good user
                    System.out.println("Signup: Creating user with: " + username + " " + password);
                    if (!userDAO.addUser(username, password, email)) {
                        // duplicate user
                        root.put("username_error", "Username already in use, Please choose another");

                        cfg.getTemplate("signup.ftl").process(root, writer);
                    } else {
                        // good user, let's start a session
                        String sessionID = sessionDAO.startSession(username);
                        System.out.println("Session ID is" + sessionID);

                        response.raw().addCookie(new Cookie("session", sessionID));
                        response.redirect("/welcome");
                    }
                } else {
                    // bad signup
                    System.out.println("User Registration did not validate");
                    cfg.getTemplate("signup.ftl").process(root, writer);
                }

                return writer;
            }
        });

        // present signup form for blog
        Spark.get("/signup", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                SimpleHash root = new SimpleHash();

                // initialize values for the form.
                root.put("username", "");
                root.put("password", "");
                root.put("email", "");
                root.put("password_error", "");
                root.put("username_error", "");
                root.put("email_error", "");
                root.put("verify_error", "");

                StringWriter writer = new StringWriter();
                cfg.getTemplate("signup.ftl").process(root, writer);
                return writer;
            }
        });

        Spark.get("/welcome", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                String cookie = getSessionCookie(request);
                String username = sessionDAO.findUserNameBySessionId(cookie);

                if (username == null) {
                    System.out.println("welcome() can't identify the user, redirecting to signup");
                    response.redirect("/signup");

                }
                else {
                    SimpleHash root = new SimpleHash();

                    root.put("username", username);

                    cfg.getTemplate("welcome.ftl").process(root, writer);
                }

                return writer;
            }
        });

        // will present the form used to process new blog posts
        Spark.get("/newpost", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                // get cookie
                String username = sessionDAO.findUserNameBySessionId(getSessionCookie(request));

                if (username == null) {
                    // looks like a bad request. user is not logged in
                    response.redirect("/login");
                } else {
                    SimpleHash root = new SimpleHash();
                    root.put("username", username);

                    cfg.getTemplate("newpost_template.ftl").process(root, writer);
                }

                return writer;
            }
        });

        // handle the new post submission
        Spark.post("/newpost", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                String title = StringEscapeUtils.escapeHtml4(request.queryParams("subject"));
                String post = StringEscapeUtils.escapeHtml4(request.queryParams("body"));
                String tags = StringEscapeUtils.escapeHtml4(request.queryParams("tags"));

                String username = sessionDAO.findUserNameBySessionId(getSessionCookie(request));

                if (username == null) {
                    response.redirect("/login");    // only logged in users can post to blog
                }
                else if (title.equals("") || post.equals("")) {
                    // redisplay page with errors
                    HashMap<String, String> root = new HashMap<String, String>();
                    root.put("errors", "post must contain a title and blog entry.");
                    root.put("subject", title);
                    root.put("username", username);
                    root.put("tags", tags);
                    root.put("body", post);
                    cfg.getTemplate("newpost_template.ftl").process(root, writer);
                }
                else {
                    // extract tags
                    ArrayList<String> tagsArray = extractTags(tags);

                    // substitute some <p> for the paragraph breaks
                    post = post.replaceAll("\\r?\\n", "<p>");

                    String permalink = blogPostDAO.addPost(title, post, tagsArray, username);

                    // now redirect to the blog permalink
                    response.redirect("/post/" + permalink);
                }

                return writer;
            }
        });

        // process a new comment
        Spark.post("/newcomment", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();

                String name = StringEscapeUtils.escapeHtml4(request.queryParams("commentName"));
                String email = StringEscapeUtils.escapeHtml4(request.queryParams("commentEmail"));
                String body = StringEscapeUtils.escapeHtml4(request.queryParams("commentBody"));
                String permalink = request.queryParams("permalink");

                Document post = blogPostDAO.findByPermalink(permalink);
                if (post == null) {
                    response.redirect("/post_not_found");
                }
                // check that comment is good
                else if (name.equals("") || body.equals("")) {
                    // bounce this back to the user for correction
                    SimpleHash root = new SimpleHash();
                    SimpleHash comment = new SimpleHash();

                    comment.put("name", name);
                    comment.put("email", email);
                    comment.put("body", body);
                    root.put("comments", comment);
                    root.put("post", post);
                    root.put("errors", "Post must contain your name and an actual comment");

                    cfg.getTemplate("entry_template.ftl").process(root, writer);
                } else {
                    blogPostDAO.addPostComment(name, email, body, permalink);
                    response.redirect("/post/" + permalink);
                }

                return writer;
            }
        });

        // present the login page
        Spark.get("/login", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                SimpleHash root = new SimpleHash();

                root.put("username", "");
                root.put("login_error", "");

                cfg.getTemplate("login.ftl").process(root, writer);
                return writer;
            }
        });

        // process output coming from login form. On success redirect folks to the welcome page
        // on failure, just return an error and let them try again.
        Spark.post("/login", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                String username = request.queryParams("username");
                String password = request.queryParams("password");

                System.out.println("Login: User submitted: " + username + "  " + password);

                Document user = userDAO.validateLogin(username, password);

                if (user != null) {

                    // valid user, let's log them in
                    String sessionID = sessionDAO.startSession(user.get("_id").toString());

                    if (sessionID == null) {
                        response.redirect("/internal_error");
                    }
                    else {
                        // set the cookie for the user's browser
                        response.raw().addCookie(new Cookie("session", sessionID));

                        response.redirect("/welcome");
                    }
                }
                else {
                    SimpleHash root = new SimpleHash();


                    root.put("username", StringEscapeUtils.escapeHtml4(username));
                    root.put("password", "");
                    root.put("login_error", "Invalid Login");
                    cfg.getTemplate("login.ftl").process(root, writer);
                }

                return writer;
            }
        });

        // tells the user that the URL is dead
        Spark.get("/post_not_found", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                SimpleHash root = new SimpleHash();
                cfg.getTemplate("post_not_found.ftl").process(root, writer);

                return writer;
            }
        });

        // allows the user to logout of the blog
        Spark.get("/logout", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                String sessionID = getSessionCookie(request);

                if (sessionID == null) {
                    // no session to end
                    response.redirect("/login");
                }
                else {
                    // deletes from session table
                    sessionDAO.endSession(sessionID);

                    // this should delete the cookie
                    Cookie c = getSessionCookieActual(request);
                    c.setMaxAge(0);

                    response.raw().addCookie(c);

                    response.redirect("/login");
                }

                return new StringWriter();
            }
        });

        // used to process internal errors
        Spark.get("/internal_error", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                StringWriter writer = new StringWriter();
                SimpleHash root = new SimpleHash();

                root.put("error", "System has encountered an error.");
                cfg.getTemplate("error_template_not_found.ftl").process(root, writer);
                return writer;
            }
        });
    }

    // helper function to get session cookie as string
    private String getSessionCookie(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // helper function to get session cookie as string
    private Cookie getSessionCookieActual(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie;
            }
        }
        return null;
    }

    // tags the tags string and put it into an array
    private ArrayList<String> extractTags(String tags) {

        // probably more efficent ways to do this.
        //
        // whitespace = re.compile('\s')

        tags = tags.replaceAll("\\s", "");
        String tagArray[] = tags.split(",");

        // let's clean it up, removing the empty string and removing dups
        ArrayList<String> cleaned = new ArrayList<String>();
        for (String tag : tagArray) {
            if (!tag.equals("") && !cleaned.contains(tag)) {
                cleaned.add(tag);
            }
        }

        return cleaned;
    }

    // validates that the registration form has been filled out right and username conforms
    public boolean validateSignup(String username, String password, String verify, String email,
                                  HashMap<String, String> errors) {
        String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
        String PASS_RE = "^.{3,20}$";
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

        errors.put("username_error", "");
        errors.put("password_error", "");
        errors.put("verify_error", "");
        errors.put("email_error", "");

        if (!username.matches(USER_RE)) {
            errors.put("username_error", "invalid username. try just letters and numbers");
            return false;
        }

        if (!password.matches(PASS_RE)) {
            errors.put("password_error", "invalid password.");
            return false;
        }


        if (!password.equals(verify)) {
            errors.put("verify_error", "password must match");
            return false;
        }

        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                errors.put("email_error", "Invalid Email Address");
                return false;
            }
        }

        return true;
    }

    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(BlogController.class, "/blog");
        return retVal;
    }
}
