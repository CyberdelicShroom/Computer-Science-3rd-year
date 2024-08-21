const express = require('express');
const app = express(); // setting up the application
const mongoose = require("mongoose");
const cors = require('cors');
const bodyParser = require("body-parser");
//User routes
const userRoute =  require("./routes/users.js");
const auth = require("./routes/authenicateUser.js");
//Handle posts

//santize our requests to the database
const mongoSanitize = require("express-mongo-sanitize");
//These are our middleware
const dotenv = require("dotenv");
const helmet =  require("helmet");
const morgan = require("morgan");



dotenv.config() // don't think this is neccessary since gitlab is private anyway but I'll leave it here for now
const port = process.env.PORT || 5000;

const URI_Database = "mongodb+srv://group2:Group123@cluster0.ijou5.mongodb.net/socialConnect?retryWrites=true&w=majority";

mongoose.connect(URI_Database, { useNewUrlParser: true, useUnifiedTopology: true }).then(() => console.log('connected')).catch(e => console.log(e));

//Middleware
app.use(cors());
app.use(bodyParser.urlencoded({ limit: '30mb', extended: true }));
app.use(express.json());
app.use(helmet());
app.use(morgan("common"));
app.use("/api/users", userRoute);
app.use("/api/auth", auth);
app.use(mongoSanitize()); //use default values


//Routes
app.get("/", (req, res) => {
    res.send("Welcome to homepage");
})


//Setting uo the server, it will run on port 5000
app.listen(port, () => {
    console.log(`Server running on port ${port}`);
})

