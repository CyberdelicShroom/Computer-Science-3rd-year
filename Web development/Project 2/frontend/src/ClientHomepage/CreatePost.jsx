import React, { useState } from "react";
import Navbar from "../components/navbar/Navbar";
import { Formik, Field } from "formik";
const Axios = require('axios');

const CreatePost = () => {

    const [img_file, setImgFile] = useState();
    const [vid_file, setVidFile] = useState();

    function postContent(values){

        if(navigator.geolocation){
            // console.log("Geolocation is avaliable");
            navigator.geolocation.getCurrentPosition(position => {
                let lat = position.coords.latitude;
                let long = position.coords.longitude;
                let coords = [lat, long];

                if(img_file != null && vid_file != null){
                    const imgData = new FormData();
                    imgData.append("file_img", img_file);
                    let filename_img = img_file.name;

                    const vidData = new FormData();
                    vidData.append("file_vid", vid_file);
                    let filename_vid = vid_file.name;

                    Axios.post("api/posts", {
                        caption: values.caption,
                        image: filename_img,
                        video: filename_vid,
                        geolocation: coords
                    })
                    .then((res) => {
                        console.log(res.data);
                        Axios.post('/upload', imgData).then(res => console.log({"Image posted": res}));
                        Axios.post('/upload_vid', vidData).then(res => console.log({"Video posted": res}));
                    });
                } else if (img_file != null && vid_file == null){
                    const imgData = new FormData();
                    imgData.append("file_img", img_file);
                    let filename_img = img_file.name;
                    let filename_vid = "";

                    Axios.post("api/posts", {
                        caption: values.caption,
                        image: filename_img,
                        video: filename_vid,
                        geolocation: coords
                    })
                    .then((res) => {
                        console.log(res.data);
                        Axios.post('/upload', imgData).then(res => console.log({"Image posted": res}));
                    });
                } else if(img_file == null && vid_file != null){
                    const vidData = new FormData();
                    vidData.append("file_vid", vid_file);
                    let filename_vid = vid_file.name;
                    let filename_img = "";
                    Axios.post("api/posts", {
                        caption: values.caption,
                        image: filename_img,
                        video: filename_vid,
                        geolocation: coords
                    })
                    .then((res) => {
                        console.log(res.data);
                        Axios.post('/upload_vid', vidData).then(res => console.log({"Video posted": res}));
                    });
                } else {
                    let filename_img = "";
                    let filename_vid = "";
                    Axios.post("api/posts", {
                        caption: values.caption,
                        image: filename_img,
                        video: filename_vid,
                        geolocation: coords
                    })
                    .then((res) => {
                        console.log(res.data);
                    });
                }
            });
        } else {
            console.log("Geolocation is unavaliable");
        }

    }

    return (
        <><div>
            <Navbar isDev/>
        </div><>
        <Formik
            initialValues={{ caption: "" }}
            onSubmit={(values) => {
                postContent(values);
            } }
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <Field name="caption" placeholder="Caption" />
            {props.touched.caption && props.errors.caption && <div>{props.errors.caption}</div>}
            <div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Post image:</label>
                <input style={{ marginBottom: "1rem" }}
                    type="file"
                    name="image"
                    onChange={event => {
                        const file = event.target.files[0];
                        setImgFile(file);
                    } } />
            </div>
            <div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Post video:</label>
                <input style={{ marginBottom: "1rem" }}
                    type="file"
                    name="video"
                    onChange={event => {
                        const file = event.target.files[0];
                        setVidFile(file);
                    } } />
            </div>

            <button type="submit">Post</button>
        </form>)}
        </Formik></></>)

}

export default CreatePost;