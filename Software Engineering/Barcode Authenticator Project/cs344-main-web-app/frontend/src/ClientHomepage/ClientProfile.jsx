import { useNavigate } from "react-router-dom";
import { useState } from "react";
import Axios from 'axios';
// import Navbar from "../components/navbar/Navbar";

const ClientProfile = () => {
    const [username, setUserName] = useState();
    const [email, setEmail] = useState();
    const [imgSrc, setImgSrc] = useState();
    const [timestamp, setTime] = useState();

    const navigate = useNavigate();

    async function deleteClientProfile() {
        Axios.delete("api/user/delete").then(res => {
            console.log(res.data);
            navigate("/client_signin");
        });
    }

    async function logout() {
        Axios.get("api/user/logout").then(res => {
            console.log(res.data);
            navigate("/client_signin");
        });
    }

    async function getUserDetails() {
        Axios.get("api/user/details").then(res => {
            let imgPath = "images/"+res.data.image;
            setImgSrc(imgPath);
            setUserName(res.data.username);
            setEmail(res.data.email);
            setTime(res.data.timestamp);
        });
    }

    getUserDetails();

    return (
        <div>
            {/* <Navbar isDev /> */}
            <img id="pfp" alt={imgSrc} src={imgSrc} height="200" width="300"></img>
            <label>Username:</label>
            <p>{username}</p>
            <label>Email:</label>
            <p>{email}</p>
            <label>Date Created:</label>
            <p>{timestamp}</p>
            
            
            <button onClick={() => {
                const confirmation = window?.confirm("Are you sure you want to delete your profile?");
                if (confirmation) {
                    deleteClientProfile();
                }
            }}>Delete Profile</button>

            <button onClick={() => { navigate("/update_client_profile"); }}>Edit Profile</button>
            <button onClick={() => { logout(); }}>Log out</button>
        </div>
    );
};
export default ClientProfile;
