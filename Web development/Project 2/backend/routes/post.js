const express = require('express')
const router = express.Router()
const {
  getPosts,
  setPost,
  updatePost,
  deletePost,
} = require('../handlers/handlePosts')

const { validateToken } = require('../middleware/verifyJWT')

router.route('/').get(validateToken, getPosts).post(validateToken, setPost)
router.route('/:id').delete(validateToken, deletePost).put(validateToken, updatePost)

module.exports = router