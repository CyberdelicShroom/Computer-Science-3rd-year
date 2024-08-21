const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const QRcodeIDSchema = new Schema({
    QRCodeID:{
        type:String,
        required:true,
    },
    createdAt: { 
        type: Date, 
        expires: 300
    }
})

module.exports = mongoose.model('QRCodes', QRcodeIDSchema);