const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const postSchema = new Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'Users',
    },
    caption: {
        type: String
    },
    image: {
        type: String
    },
    video: {
        type: String
    },
    timestamp: {
        type: String
    },
    geolocation: {
        type: Array
    }
});

module.exports = mongoose.model('Posts', postSchema);