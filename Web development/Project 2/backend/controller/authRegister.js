//This is to authenicate and register user
const bcrypt = require('bcryptjs');
const User = require("../models/users");
var geolocation = require('geolocation');
const jwt = require("jsonwebtoken");

const registerUser = async (req, res) => {


    const { username, email, password, geolocation } = req.body;

    const userExists = await User.exists({ email:email});

    if (userExists) {
      return res.status(409).send("E-mail already in use.");
    }
    
    try {
        //encrypt the password
        const encryptedPassword = await bcrypt.hash(password, 10);

        //create and store the new user
        const user = await User.create({
            username: username,
            email: email,
            password: encryptedPassword
        });

        const token = jwt.sign(
            {
                userId: user._id,
                email,
            },
            process.env.TOKEN_KEY,
            {
                expiresIn: "24h",
            }
        );

        res.status(201).json({
            userDetails: {
              email: user.email,
              token: token,
              username: user.username,
            },
        });
        
        
    } catch (err) {
        res.status(500).json({ 'message': err.message });
    }
}


module.exports = registerUser;