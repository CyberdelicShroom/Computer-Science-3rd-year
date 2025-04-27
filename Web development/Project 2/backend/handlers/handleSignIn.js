const User = require("../models/User");
const bcrypt = require('bcrypt');
const { createToken } = require('../middleware/verifyJWT')
const { scryptSync, timingSafeEqual } = require('crypto');

const handleSignIn = async (req, res) => {

    const { email, password } = req.body;
    
    if (!password || !email) return res.status(400).json({ 'message': 'Email and password are required.' });
   
    // check if user exists in the db
    // const user_exists = await User.findOne({ email }).exec();
    const user_exists = await User.findOne({ email: email });
    
    if (!user_exists){
        res.json({details: "User does not exist"}); //Conflict 
    } else {
        const [salt, key] = user_exists.password.split(':');
        const hashedBuffer = scryptSync(password, salt, 64);
    
        const keyBuffer = Buffer.from(key, 'hex');
        const match = timingSafeEqual(hashedBuffer, keyBuffer);
        
        if (match) {
            let accessToken = createToken(user_exists._id);

            res.cookie("access-token", accessToken, {
                maxAge: 12000000 //20min
            })
            res.json({
                status: 'success',
                token: accessToken,
            });
        } else {
            res.json({details: "Password incorrect"});
        }
    }

    // if (!user_exists){
    //     res.json({details: "User does not exist"}); //Conflict 
    // } else if(await bcrypt.compare(password, user_exists.password)){
        
    //     let accessToken = createToken(user_exists._id);

    //     res.cookie("access-token", accessToken, {
    //         maxAge: 12000000 //20min
    //     })
    //     res.json({
    //         status: 'success',
    //         token: accessToken,
    //     });
    // } else {
    //     res.json({details: "Password incorrect"});
    // }
}

module.exports = { handleSignIn };