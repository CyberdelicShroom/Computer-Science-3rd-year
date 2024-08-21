require('dotenv').config();
// const jwt = require('jsonwebtoken');
// const asyncHandler = require('express-async-handler');
const User = require('../models/User');
const { sign, verify } = require("jsonwebtoken");
const asyncHandler = require('express-async-handler')

const createToken = (id) => {
    const accessToken = sign({ id },
    process.env.ACCESS_TOKEN_SECRET, { expiresIn: '20m' });

  return accessToken;
};

const validateToken = asyncHandler(async (req, res, next) => {
  const accessToken = req.cookies["access-token"];

    if(!accessToken){
        res.status(401);
        throw new Error('Not authorized, no token');
    }

    try {
        const decoded = verify(accessToken, process.env.ACCESS_TOKEN_SECRET);

        req.user = await User.findById(decoded.id);

        next();
    } catch (error) {
        console.log(error);
        res.status(401);
        throw new Error('Not authorized');
    }

});

module.exports = { createToken, validateToken };