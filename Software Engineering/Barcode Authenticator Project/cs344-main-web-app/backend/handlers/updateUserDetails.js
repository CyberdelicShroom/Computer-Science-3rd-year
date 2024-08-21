const User = require("../models/User");
const bcrypt = require('bcrypt');

const updateUserDetails = async (req, res) => {

    const { username, email, password, image, uuid } = req.body;
    // check for duplicate users in the db
    const duplicate = await User.findOne({ email: email });
    const userID = req.user.id;
    const user = await User.findById(userID);
    
    if (duplicate){
        res.json({details: "User already exists, try use a different email."}); //Conflict 
    } else if(await bcrypt.compare(password, user.password)){
        let updatedUser;
        let payload;
        if(username != "" && email != "" && image != ""){
            payload = { username: username, email: email, image: image };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username != "" && email == "" && image == ""){
            payload = { username: username };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username == "" && email != "" && image == ""){
            payload = { email: email };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username == "" && email == "" && image != ""){
            payload = { image: image };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username != "" && email != "" && image == ""){
            payload = { username: username, email: email };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username == "" && email != "" && image != ""){
            payload = { email: email, image:image };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(username != "" && email == "" && image != ""){
            payload = { username: username, image: image };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        } else if(uuid != "" && user.uuid == null) {
            payload = { uuid: uuid };
            updatedUser = await User.findByIdAndUpdate(userID, payload, {
                new: true,
            });
        }

        res.json({
            status: 'success',
            user: updatedUser
        });
    } else {
        res.json({   
            status: "failure",
            details: "Password incorrect"
        });
    }
}

module.exports = { updateUserDetails };