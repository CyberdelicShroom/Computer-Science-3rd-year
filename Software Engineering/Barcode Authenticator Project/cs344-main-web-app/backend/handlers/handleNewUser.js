const User = require("../models/User");
const bcrypt = require('bcrypt');
const { createToken } = require('../middleware/verifyJWT')

const handleNewUser = async (req, res) => {

    const { username, email, password, geolocation, image } = req.body;
    if (!username || !password || !email) return res.status(400).json({ 'message': 'Username, email and password are required.' });
    
    // check for duplicate usernames in the db
    const duplicate = await User.findOne({ email: email }).exec();
    if (duplicate) return res.json({details: "User already exists, try use a different email."}); //Conflict 

    //encrypt the password

    const hashedPwd = await bcrypt.hash(password, 10);

    const timestamp = Date.now();
    const dateString = new Date(timestamp).toLocaleString();
    
    //create and store the new user
    const user = new User({
        username: username,
        email: email,
        password: hashedPwd,
        timestamp: dateString,
        geolocation: geolocation,
        image: image,
        uuid: null,
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

}

module.exports = { handleNewUser };