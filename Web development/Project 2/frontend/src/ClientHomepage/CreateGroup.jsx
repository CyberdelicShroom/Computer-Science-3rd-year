import React, { useState, useEffect } from "react";
import Navbar from "../components/navbar/Navbar";
import { Formik, Field } from "formik";
import ListOfUsers from "./ListOfUsers";
const Axios = require('axios');

const CreateGroup = () => {

    const [img_file, setImgFile] = useState();
    const [users, setUsers] = useState();

    function createGroup(values){

        if (img_file != null){
            const imgData = new FormData();
            imgData.append("file_img", img_file);
            let filename_img = img_file.name;

            Axios.post("api/groups", {
                name: values.name,
                users: values.users,
                image: filename_img,
            })
            .then((res) => {
                console.log(res.data);
                Axios.post('/upload', imgData).then(res => console.log({"Image posted": res}));
                Axios.put("api/user/add_group", {
                    group: values.name,
                    members: values.users
                })
                .then((res) => {
                    console.log({"Users groups": res.data});
                })
            });
        } else {
            let filename_img = "";
            Axios.post("api/groups", {
                name: values.name,
                users: values.users,
                image: filename_img,
            })
            .then((res) => {
                console.log({"Group added": res.data});
                Axios.put("api/user/add_group", {
                    group: values.name,
                    members: values.users
                })
                .then((res) => {
                    console.log({"Users in new group": res.data});
                })
            });
        }
    }

    useEffect(() => {
        selectMembers();
    }, [])

    function selectMembers(){
        Axios.get("api/user/all").then(res => {
            if(res){
                // console.log(res.data);
                let users = res.data;
                setUsers(users);
            }
        })
    }
    
    return (
        <><div>
            <Navbar isDev/>
        </div><>
        
        <Formik
            initialValues={{ name: "", users: []}}
            onSubmit={(values) => {
                createGroup(values);
            } }
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <Field name="name" placeholder="Group Name" />
            {props.touched.name && props.errors.name && <div>{props.errors.name}</div>}
            <div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Group Profile Picture:</label>
                <input style={{ marginBottom: "1rem" }}
                    type="file"
                    name="image"
                    onChange={event => {
                        const file = event.target.files[0];
                        setImgFile(file);
                    } } />
            </div>
            <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Select which users you want to add:</label>
            <ListOfUsers users={users} />
            <button type="submit">Create</button>
        </form>)}
        </Formik></></>)

}

export default CreateGroup;