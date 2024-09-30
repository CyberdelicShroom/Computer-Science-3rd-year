const User = require("../models/User");
const Post = require("../models/Post");
const asyncHandler = require('express-async-handler');

const handleUserDeletion = asyncHandler(async (req, res) => {
   
    const userID = req.user.id;
    const user_logged_in = await User.findById(userID);
    let username = user_logged_in.username;
    await Post.deleteMany({ user: userID});
    await User.findByIdAndDelete(userID);

    res.json(`User '${username}', has been deleted as well as all their posts.`);
});

module.exports = { handleUserDeletion };