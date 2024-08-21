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
    geolocation:{
        type:Array
    },
    image:{
        type:String,
    },
    Admin:{
        type:Boolean,
        default:false,
    },
    groups:{
        type: Array,
        default:[]
    }

})

module.exports = mongoose.model('Users', userSchema);