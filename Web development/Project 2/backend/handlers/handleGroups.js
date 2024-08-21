const asyncHandler = require('express-async-handler')

const Group = require('../models/Group')
const User = require('../models/User')

// @desc    Get groups
// @route   GET /api/group
// @access  Private
const getGroups = asyncHandler(async (req, res) => {
    let user = await User.findById(req.user.id);
    let email = user.email;
    const groups = await Group.find({ users: email })

    res.status(200).json(groups)
})

// @desc    Set group
// @route   POST /api/group
// @access  Private
const setGroup = asyncHandler(async (req, res) => {
    const { name, image, users} = req.body;
    const timestamp = Date.now();
    const dateString = new Date(timestamp).toLocaleString();
    let imgPath = "images/"+image;
    
    const group = await Group.create({
        admin: req.user.id,
        name: name,
        users: users,
        image: imgPath,
        timestamp: dateString
    })

    res.status(200).json(group)
})

// @desc    Update group
// @route   PUT /api/group/:id
// @access  Private
const updateGroup = asyncHandler(async (req, res) => {
    const group = await Group.findById(req.params.id)

    if (!group) {
        res.status(400)
        throw new Error('Group not found')
    }

    // Check for user
    if (!req.user) {
        res.status(401)
        throw new Error('User not found')
    }

    // Make sure the logged in user matches the goal user
    if (group.admin.toString() !== req.user.id) {
        res.status(401)
        throw new Error('User not authorized')
    }

    let members = group.users;
    for(let i = 0; i<members.length; i++){
        await User.findOneAndUpdate({email: members[i]}, {$pull: {groups: group.name}});
    }

    const updatedGroup = await Group.findByIdAndUpdate(req.params.id, req.body, {
        new: true,
    })

    for(let i = 0; i<members.length; i++){
        await User.findOneAndUpdate({email: members[i]}, {$push: {groups: updatedGroup.name}});
    }
    res.status(200).json(updatedGroup)
})

// @desc    Delete group
// @route   DELETE /api/group/:id
// @access  Private
const deleteGroup = asyncHandler(async (req, res) => {
    const group = await Group.findById(req.params.id)
    // console.log({"user group": group.user.toString(), "userID": req.user.id});

    if (!group) {
        res.status(400)
        throw new Error('Group not found')
    }

    // Check for user
    if (!req.user) {
        res.status(401)
        throw new Error('User not found')
    }

    // Make sure the logged in user matches the goal user
    if (group.admin.toString() !== req.user.id) {
        res.status(401)
        throw new Error('User not authorized')
    }
    
    await group.remove()

    let members = group.users;
    for(let i = 0; i<members.length; i++){
        await User.findOneAndUpdate({email: members[i]}, {$pull: {groups: group.name}});
    }

    const user = await User.findById(req.user.id)
    let username = user.username;
    
    res.status(200).json(`${username} deleted a group with id '${req.params.id}'.`)
})

module.exports = {
    getGroups,
    setGroup,
    updateGroup,
    deleteGroup
}