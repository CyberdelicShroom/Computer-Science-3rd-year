const User = require("../models/User");
const asyncHandler = require('express-async-handler');

const getUserDetails = asyncHandler(async (req, res) => {
   const userID = req.user.id;
   const user_logged_in = await User.findById(userID);
   let image = user_logged_in.image;
   let username = user_logged_in.username;
   let email = user_logged_in.email;
   let timestamp = user_logged_in.timestamp;
   res.json({image: image, username: username, email: email, timestamp: timestamp});
})

module.exports = { getUserDetails };