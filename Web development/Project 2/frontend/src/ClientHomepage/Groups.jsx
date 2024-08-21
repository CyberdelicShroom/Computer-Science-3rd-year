import React, { useState, useEffect } from "react";
import Navbar from "../components/navbar/Navbar";
import Axios from "axios";
import { useNavigate } from 'react-router-dom';
import ListOfGroups from "./ListOfGroups";

const Groups = () => {
    const [groups, setGroups] = useState();
    let navigate = useNavigate();
    useEffect(() => {
        getGroups();
    }, [])

    function getGroups(){
        Axios.get("api/groups")
        .then((res) => {
            if(res){
                let groups = res.data;
                setGroups(groups);
            }

        });
    }

    async function create(){
        navigate("/create_group");
    }

    return (
        <div>
            <Navbar isDev />
            <button onClick={create}>Create new group</button>
            <ListOfGroups groups={groups}/>
        </div>
    );
};
export default Groups;