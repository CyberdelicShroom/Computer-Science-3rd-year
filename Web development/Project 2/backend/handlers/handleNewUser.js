const User = require("../models/User");
const bcrypt = require('bcrypt');
const { createToken } = require('../middleware/verifyJWT');
const { scryptSync, randomBytes, timingSafeEqual } = require('crypto');

const handleNewUser = async (req, res) => {

    const { username, email, password, geolocation, image } = req.body;
    if (!username || !password || !email) return res.status(400).json({ 'message': 'Username, email and password are required.' });
    
    // check for duplicate usernames in the db
    const duplicate = await User.findOne({ email: email }).exec();
    if (duplicate) return res.json({details: "User already exists, try use a different email."}); //Conflict 

    //encrypt the password with salt
    const salt = randomBytes(16).toString('hex');
    const hashedPassword = scryptSync(password, salt, 64).toString('hex');
    // const hashedPwd = await bcrypt.hash(password, 10);

    const timestamp = Date.now();
    const dateString = new Date(timestamp).toLocaleString();
    
    //create and store the new user
    const user = new User({
        username: username,
        email: email,
        password: `${salt}:${hashedPassword}`,
        timestamp: dateString,
        geolocation: geolocation,
        image: image,
    });
    await user.save();

    let accessToken = createToken(user._id);

    res.cookie("access-token", accessToken, {
        maxAge: 1200000 //20min
    })

    res.status(200).json({
        status: 'success',
        //The data that the client sent to the server and is now being sent back to the client
        timestamp: dateString,
        token: accessToken,
        data: req.body
    })

    // res.json({
    //     status: 'success',
    //     //The data that the client sent to the server and is now being sent back to the client
    //     timestamp: dateString,
    //     token: accessToken,
    //     data: req.body
    // });
    // console.log("Actually before res return");
    // return res;
}

module.exports = { handleNewUser };