const User = require("../models/User");
const asyncHandler = require('express-async-handler');

const addGroup = asyncHandler(async (req, res) => {
    // const userID = req.user.id;
    const { group, members } = req.body;

    for(let i = 0; i<members.length; i++){
        await User.findOneAndUpdate({email: members[i]}, {$push: {groups: group}});
    }

   res.json(`Added users to ${group}`);
})

module.exports = { addGroup };