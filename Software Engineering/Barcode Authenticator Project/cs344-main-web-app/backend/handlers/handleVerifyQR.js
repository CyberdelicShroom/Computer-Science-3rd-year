const User = require("../models/User");
const QRCodeID = require("../models/QRCodeID");
const { createToken } = require('../middleware/verifyJWT')

var logmein = "false"
var emailtouse = null

const handleVerifyQR = async (req, res) => {

    const { email, qr, uuid } = req.body;

    const user = await User.findOne({ email: email });

    if (user.uuid != uuid) {
        res.status(400).json({
            status: 'failure',
        });
    } else {
        const qrcode = await QRCodeID.findOne({ QRCodeID: qr });
        
        if (qrcode != null) {
            logmein = true
            emailtouse = email
            console.log(logmein)
            res.status(200).json({
                status: 'success',
            });
        } else {
            res.status(400).json({
                status: 'failure',
            });
        }
    }
}

const handleCheck = async (req, res) => {

        if (logmein == "false") {
            res.json({status: 'failure'});
        } else {
            const user_exists = await User.findOne({ email: emailtouse });
            console.log(user_exists)
    
            let accessToken = createToken(user_exists._id);
    
            res.cookie("access-token", accessToken, {
                maxAge: 12000000 //20min
            })
            res.json({
                status: 'success',
                username: user_exists.username,
                token: accessToken,
            });
            logmein = "false"        
        }
    }

module.exports = { handleCheck, handleVerifyQR };
