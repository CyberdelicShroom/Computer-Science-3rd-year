const asyncHandler = require('express-async-handler')

const Post = require('../models/Post')
const User = require('../models/User')

// @desc    Get posts
// @route   GET /api/posts
// @access  Private
const getPosts = asyncHandler(async (req, res) => {
    const posts = await Post.find({ user: req.user.id })

    res.status(200).json(posts)
})

// @desc    Set post
// @route   POST /api/posts
// @access  Private
const setPost = asyncHandler(async (req, res) => {
    const { caption, image, video, geolocation } = req.body;
    const timestamp = Date.now();
    const dateString = new Date(timestamp).toLocaleString();
    let imgPath = "images/"+image;
    let vidPath = "videos/"+video;
    
    const post = await Post.create({
        user: req.user.id,
        caption: caption,
        image: imgPath,
        video: vidPath,
        timestamp: dateString,
        geolocation: geolocation
    })

    res.status(200).json(post)
})

// @desc    Update post
// @route   PUT /api/posts/:id
// @access  Private
const updatePost = asyncHandler(async (req, res) => {
    const post = await Post.findById(req.params.id)

    if (!post) {
        res.status(400)
        throw new Error('Post not found')
    }

    // Check for user
    if (!req.user) {
        res.status(401)
        throw new Error('User not found')
    }

    // Make sure the logged in user matches the goal user
    if (post.user.toString() !== req.user.id) {
        res.status(401)
        throw new Error('User not authorized')
    }

    const updatedPost = await Post.findByIdAndUpdate(req.params.id, req.body, {
        new: true,
    })

    res.status(200).json(updatedPost)
})

// @desc    Delete post
// @route   DELETE /api/posts/:id
// @access  Private
const deletePost = asyncHandler(async (req, res) => {
    const post = await Post.findById(req.params.id)
    console.log({"user post": post.user.toString(), "userID": req.user.id});
    if (!post) {
        res.status(400)
        throw new Error('Post not found')
    }

    // Check for user
    if (!req.user) {
        res.status(401)
        throw new Error('User not found')
    }

    // Make sure the logged in user matches the goal user
    if (post.user.toString() !== req.user.id) {
        res.status(401)
        throw new Error('User not authorized')
    }
    
    await post.remove()

    const user = await User.findById(req.user.id)
    let username = user.username;
    
    res.status(200).json(`${username} deleted a post with id '${req.params.id}'.`)
})

module.exports = {
    getPosts,
    setPost,
    updatePost,
    deletePost,
}