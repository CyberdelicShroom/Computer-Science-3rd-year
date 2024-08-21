const User = require("../models/User");
const asyncHandler = require('express-async-handler');

const getAllUsers = asyncHandler(async (req, res) => {
//    const userID = req.user.id;
   const users = await User.find({});

   res.json(users);
})

module.exports = { getAllUsers };