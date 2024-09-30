require('dotenv').config();
const express = require('express');
const app = express();
const path = require('path');
const cors = require('cors');
const corsOptions = require('./config/corsOptions');
const { logger } = require('./middleware/logEvents');
const errorHandler = require('./middleware/errorHandler');
const verifyJWT = require('./middleware/verifyJWT');
const cookieParser = require('cookie-parser');
// const credentials = require('./middleware/credentials');
const mongoose = require('mongoose');
const connectDB = require('./config/dbConn');
const PORT = process.env.PORT || 5000;
const multer = require("multer");

// Connect to MongoDB
connectDB();

// custom middleware logger
// app.use(logger);

// Handle options credentials check - before CORS!
// and fetch cookies credentials requirement
// app.use(credentials);

// Cross Origin Resource Sharing
// app.use(cors(corsOptions));
app.use(cors());

// built-in middleware to handle urlencoded form data
// app.use(express.urlencoded({ extended: false }));

// built-in middleware for json 
app.use(express.json());

//middleware for cookies
app.use(cookieParser());

//serve static files
// app.use('/', express.static(path.join(__dirname, '/public')));

// routes
// app.use('/', require('./routes/root'));
// app.use('/register', require('./routes/register'));
// app.use('/auth', require('./routes/auth'));
// app.use('/refresh', require('./routes/refresh'));
// app.use('/logout', require('./routes/logout'));

// app.use(verifyJWT);
// app.use('/employees', require('./routes/api/employees'));
// app.use('/users', require('./routes/api/users'));

// app.all('*', (req, res) => {
//     res.status(404);
//     if (req.accepts('html')) {
//         res.sendFile(path.join(__dirname, 'views', '404.html'));
//     } else if (req.accepts('json')) {
//         res.json({ "error": "404 Not Found" });
//     } else {
//         res.type('txt').send("404 Not Found");
//     }
// });

// app.use(errorHandler);

const user = require('./routes/user');
const post = require('./routes/post');
const group = require('./routes/group')
app.use('/api/user', user);
app.use('/api/posts', post);
app.use('/api/groups', group);

let filename;
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, "../frontend/public/images");
    },
    filename: (req, file, cb) => {
        console.log("PRINTING FILE DATA:")
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

let filename_vid;
const storage_vid = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, "../frontend/public/videos");
    },
    filename: (req, file, cb) => {
        console.log(file);
        filename_vid = file.originalname;
        // const fname = file.originalname.split(".");
        cb(null, filename_vid);
    }
});
const uploadVid = multer({storage: storage_vid});

app.post("/upload_vid", uploadVid.single("file_vid"), (req, res) => {
    // console.log(req.file.originalname);
    res.json("Video uploaded.");
    
});

mongoose.connection.once('open', () => {
    console.log('Connected to MongoDB');
    app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
});