const User = require("../models/User");

const updateUserUuid = async (req, res) => {

    const { email, uuid } = req.body;

    const user = await User.findOne({ email: email });

    let updatedUser;
    let payload;
    const user_id = user.id;

    if(uuid != "" && user.uuid == null) {
        payload = { uuid: uuid };
        updatedUser = await User.findByIdAndUpdate(user_id, payload, {
            new: true,
        });
        res.status(200).json({
            status: 'success',
        });
    } else {
        res.status(400).json({
            status: 'failure',
        });
    }
}

module.exports = { updateUserUuid };