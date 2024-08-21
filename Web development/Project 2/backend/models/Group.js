const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const groupSchema = new Schema({
    admin: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'Users',
    },
    name:{
        type: String,
        required: true,
        unique: true
    },
    users: {
        type: Array
    },
    image: {
        type: String
    },
    timestamp: {
        type: String
    },
});

module.exports = mongoose.model('Groups', groupSchema);