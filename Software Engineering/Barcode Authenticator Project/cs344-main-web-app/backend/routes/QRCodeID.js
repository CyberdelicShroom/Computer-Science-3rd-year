const router = require('express').Router();
const { handleQR } = require('../handlers/handleQR');

router.get('/getqr', handleQR);

module.exports = router;
