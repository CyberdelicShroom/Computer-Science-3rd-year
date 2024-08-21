//This file just collects all of our controllers
const verifyUser = require("./authPost");
const registerUser = require("./authRegister");

exports.controllers = { verifyUser, registerUser };