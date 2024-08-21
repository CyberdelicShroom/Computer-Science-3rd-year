const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({
    username:{
        type:String,
        required:true,
        min:3,
        max:100,
    },
    email:{
        type: String,
        required:true,
        unique:true,
        max:50,
    },
    password:{
        type: String,
        required:true,
        min:6,
    },
    timestamp:{
        type:String,
        required:true,
        default:""
    },
    image:{
        type:String,
    },
    uuid:{
        type:String,
    }
})

module.exports = mongoose.model('Users', userSchema);