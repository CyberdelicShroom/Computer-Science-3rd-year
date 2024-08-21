const express = require('express')
const router = express.Router()
const {
  getGroups,
  setGroup,
  updateGroup,
  deleteGroup
} = require('../handlers/handleGroups')

const { validateToken } = require('../middleware/verifyJWT')

router.route('/').get(validateToken, getGroups).post(validateToken, setGroup)
router.route('/:id').delete(validateToken, deleteGroup).put(validateToken, updateGroup)

module.exports = router