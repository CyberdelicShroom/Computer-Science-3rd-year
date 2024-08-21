const User = require("../models/User");
const asyncHandler = require('express-async-handler');

const handleUserLogout = asyncHandler(async (req, res) => {
   
   const userID = req.user.id;
   const user_logged_in = await User.findById(userID);
   let username = user_logged_in.username;
   res.cookie("access-token", "", {maxAge: 1});

   res.json(`${username} logged out.`);
})

module.exports = { handleUserLogout };