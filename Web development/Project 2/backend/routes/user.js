const router = require('express').Router();
const { handleNewUser } = require('../handlers/handleNewUser');
const { handleSignIn } = require('../handlers/handleSignIn');
const { handleUserDeletion } = require('../handlers/handleUserDeletion');
const { handleUserLogout } = require('../handlers/handleUserLogout');
const { validateToken } = require('../middleware/verifyJWT');
const { getUserDetails } = require('../handlers/getUserDetails');
const { updateUserDetails } = require('../handlers/updateUserDetails');
const { getAllUsers } = require('../handlers/getAllUsers');
const { addGroup } = require('../handlers/addGroup');

router.post('/register', handleNewUser);
router.post('/login', handleSignIn);
router.route('/details').get(validateToken, getUserDetails);
router.route('/update').put(validateToken, updateUserDetails);
router.route('/delete').delete(validateToken, handleUserDeletion);
router.route('/logout').get(validateToken, handleUserLogout);
router.route('/all').get(validateToken, getAllUsers);
router.route('/add_group').put(validateToken, addGroup);

module.exports = router;