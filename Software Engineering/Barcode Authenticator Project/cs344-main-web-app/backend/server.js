require('dotenv').config();
const https = require('https');
const fs = require('fs');
const express = require('express');
const app = express();
const path = require('path');
const cors = require('cors');
const corsOptions = require('./config/corsOptions');
// const { logger } = require('./middleware/logEvents');
// const errorHandler = require('./middleware/errorHandler');
const verifyJWT = require('./middleware/verifyJWT');
const cookieParser = require('cookie-parser');
// const credentials = require('./middleware/credentials');
const mongoose = require('mongoose');
const connectDB = require('./config/dbConn');
const PORT = process.env.PORT || 4000;
const multer = require("multer");

// Connect to MongoDB
connectDB();

app.use(cors());

// built-in middleware for json 
app.use(express.json());

//middleware for cookies
app.use(cookieParser());

const user = require('./routes/user');
app.use('/api/user', user);
const QRCodeID = require('./routes/QRCodeID')
app.use('/', QRCodeID)

let filename;
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, "../frontend/public/images");
    },
    filename: (req, file, cb) => {
        console.log(file);
        filename = file.originalname;
        // const fname = file.originalname.split(".");
        cb(null, filename);
    }
});
const upload = multer({storage: storage});

app.post("/upload", upload.single("file_img"), (req, res) => {
    // console.log(req.file.originalname);
    res.json("Image uploaded.");
    
});

app.get("/verifyqr", (req, res) => {

    let qr = req.body;
    console.log(qr);

    res.json({status: "success"});
});

const options = {
    key: fs.readFileSync("./config/create-cert-key.pem"),
    cert: fs.readFileSync("./config/create-cert.pem"),
};

mongoose.connection.once('open', () => {
    console.log('Connected to MongoDB');

    https.createServer(options, app).listen(PORT, () => {
        console.log(`Server running on port ${PORT}`);
      });

    // app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
});