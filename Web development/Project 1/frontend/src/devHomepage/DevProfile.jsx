import { useNavigate } from "react-router-dom";
import { useState } from "react"

const DevProfile = () => {
    const [username, setUserName] = useState();
    const [email, setEmail] = useState();
    const [experience, setExperience] = useState();
    const [imgSrc, setImgSrc] = useState();

    const navigate = useNavigate();

    async function deleteDevProfile() {
        const options = {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/deleteProfile", options);
        const data = await response.text();
        alert(data + "'s profile has been deleted.");
        navigate("/client_signin");
    }

    async function logout() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/logout", options);
        const data = await response.json();
        if(data.logout_status){
            navigate("/client_signin");
            console.log(data);
        } else {
            console.error(data);
        }
    }

    async function getMoneyMade() {
        const options = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
    }

    async function getUserName() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getDevInfo", options);
        const data = await response.json();
        setUserName(data.username);
    }

    async function getEmail() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getDevInfo", options);
        const data = await response.json();
        if(data.get_dev_info){
            setEmail(data.email);
        } else {
            console.error(data)
            alert(data.details);
            navigate("/client_signin");
        }
    }

    async function getExperience() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/getDevInfo", options);
        const data = await response.json();
        setExperience(data.experience);
    }

    async function getAvatar() {
        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
        };
        const response = await fetch("/displayAvatar", options);
        const data = await response.text();
        setImgSrc(data);
    }

    getExperience();
    getUserName();
    getEmail();
    getAvatar();

    return (
        <div>
            <img id="avatar" src={imgSrc} height="200" width="300"></img>
            <label>Username:</label>
            <p>{username}</p>
            <label>Email:</label>
            <p>{email}</p>
            <label>Work Experience:</label>
            <p>{experience}</p>
            <label>Money made from contracts:</label>
            <p id="money">N/A</p>
            
            <button onClick={() => {
                const confirmation = window?.confirm("Are you sure you want to delete your profile?");
                if (confirmation) {
                    deleteDevProfile();
                }
            }}>Delete Profile</button>
            {/* <button type="" onClick={() => { navigate("/dev_profile"); }}>View Profile</button>
                <button onClick={() => { navigate("/dev_profile"); }}>View Accepted Contracts</button>
                <button onClick={() => { navigate("/dev_profile"); }}>View Pending Contracts</button> */}

            <button onClick={() => { navigate("/update_dev_profile"); }}>Edit Profile</button>
            <button onClick={() => { logout(); }}>Log out</button>
        </div>
    );
};
export default DevProfile;


// ● Profile Page:
// 4
// ○ Preview profile as if company is viewing it (3)

// ○ Show money made from contracts so far (2) need backend to check
// ○ View accepted contracts (1) 
// ○ Show pending contracts 
// ● Edit profile details or delete account need backend