const QRCodeID = require("../models/QRCodeID");

function makeid(length) {
    var result = '';
    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for (var i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength));
    }
    return result;
}

const handleQR = async (req, res) => {

    const ID = makeid(30);

    const qrCodeID = new QRCodeID({
        QRCodeID: ID,
        createdAt: Date.now()
    });
    await qrCodeID.save();

    //send the data to the client
    res.status(200).json({
        status: 'success',
        QR: ID
    })

}

module.exports = { handleQR };