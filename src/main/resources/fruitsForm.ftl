<html>
<head>
	<title>Welcome! Choose a fruit</title>
</head>
<body>
	<form action="/favorite_fruit" method="POST">
		<h3>What's your favorite fruit?</h3>
		<#list fruits as fruit>
			<input type="radio" name="fruit" value="${fruit}">${fruit}</input><br/>
		</#list>
		<input type="submit" value="Enviar" />
	</form>
</body>
</html>