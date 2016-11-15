
// SUM
// Get all population by state
db.zips.aggregate([
  {
    $group: {
      _id: '$state',
      population: {'$sum': '$pop'}
    }
  }
])

// AVG
// Average population by state
db.zips.aggregate([
  {
    $group: {
      _id: '$state',
      average_pop: {'$avg': '$pop'}
    }
  }
])

// ADD_TO_SET
// Postal codes on each city
db.zips.aggregate([
  {
    $group: {
      _id: '$city',
      postal_codes: {'$addToSet': '$_id'}
    }
  }
])

// MAX
// Postal code with highest population on each state
db.zips.aggregate([
  {
    $group: {
      _id: {
        'my_key': '$state'
      },
      pop: {'$max': '$pop'}
    }
  }
])

// PROJECTION
// Transform zips documents into simplest version
db.zips.aggregate([
  {
    $project: {
      '_id': 0,
      'city': {$toLower: '$city'},
      'pop': 1,
      'state': 1,
      'zip': '$_id'
    }
  }
])

// MATCH
// Zip codes with population greater than 100,000 people
db.zips.aggregate([
  {
    $match: {
      'pop': {
        '$gt': 100000
      }
    }
  }
])

// Sort
// Sort by state, city
db.zips.aggregate([
  {
    $sort: {
      'state': 1,
      'city': 1
    }
  }
])

// EXERCISE
// Query the author with the highest number of comments
db.posts.aggregate([

  // Unwind comments array
  {
    $unwind: '$comments'
  },

  // Group by author and sum comments
  {
    $group: {
      _id: '$comments.author',
      'commentsCount': {$sum: 1}
    }
  },

  // Sort descending by comments count
  {
    $sort: {
      'commentsCount': -1
    }
  },

  // Get only first result
  {
    $limit: 1
  },

  // Display the data in nicely format
  {
    $project: {
      _id: 0,
      'author': '$_id',
      'comments': '$commentsCount'
    }
  }
])

// EXERCISE
// Calculate the average population of cities in California 
// (abbreviation CA) and New York (NY) (taken together) with 
// populations over 25,000
db.zips.aggregate([
  {
    $match: {
      '$and': [
        {'$or': [{'state': 'CA'}, {'state': 'NY'}]},
        {'pop': {$gt: 25000}}
      ]
    }
  },
  {
    $group: {
      _id: null,
      avgPopulation: {$avg: '$pop'}
    }
  }
])

// EXERCISE
// Calculate the sum total of people who are living in a zip 
// code where the city starts with a digit
db.zips.aggregate([

  // Get only first char and population of each city
  {
    $project: {
      _id: 0,
      first_char: {$substr: ['$city', 0, 1]},
      pop: 1
    }
  },

  // Filter only cities with digit on first char
  {
    $match: {
      first_char: {$regex: '[0-9]'}
    }
  },

  // Sum population
  {
    $group: {
      _id: null,
      pop: {$sum: '$pop'}
    }
  }
])