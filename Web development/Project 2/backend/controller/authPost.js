//this is the controller for login implementation. It verifies the user 
const bcrypt = require('bcryptjs');
const User = require("../models/users");
const jwt = require("jsonwebtoken");


const verifyUser = async (req, res) => {

    try {
        console.log(req.body);
        const { email, password } = req.body;

        const user = await User.findOne({ email: email });

        if (user && (await bcrypt.compare(password, user.password))) {
            // send new token
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

            return res.status(200).json({
                userDetails: {
                    mail: user.mail,
                    token: token,
                    username: user.username,
                },
            });
        }

        return res.status(400).send("Invalid credentials. Please try again");

    } catch (error) {
        console.log(error);
        return res.status(500).send("Something went wrong. Please try again");
    }
}

module.exports = verifyUser;