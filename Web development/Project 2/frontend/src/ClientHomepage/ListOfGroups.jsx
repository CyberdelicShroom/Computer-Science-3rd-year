import Axios from 'axios';
import { useState } from "react";

export default function ListOfGroups(props){

    function handleImage(group){
        if(group.image.split('/')[1] !== ""){
            return (
                <><img alt={group.image} src={group.image} height="50" width="150" /><br/></>
            );
        }
    }

    function ListOfUsers(props){

        const DisplayUsers = (props) => {
            const { group } = props;
            let users = group.users;
            if(users?.length > 0){
                return (
                    users.map((user, index) => {
                        return(
                            <div className="user" key={user._id}>
                                <li>{user}</li>
                            </div>
                        )
                    })
                )
            }
        } 
    
        return (
            <>{DisplayUsers(props)}</>
        )
    }

    const DisplayGroups = (props) => {
        const {groups} = props;
        const [delete_msg, setDeleteMsg] = useState();

        if(groups?.length > 0){
            return (
                groups.map((group, index) => {
                    return(
                        <div className="group" key={group._id}>
                            
                            <h3>{group.name}</h3>

                            {handleImage(group)}

                            <p>Group members:</p>
                            <ListOfUsers group={group}/><br/>

                            <p>Date created: {group.timestamp}</p>

                            <button onClick={() => {
                                const confirmation = window?.confirm("Are you sure you want to delete this group?");
                                if(confirmation){
                                    Axios.delete(`/api/groups/${group._id}`).then(res => {
                                        console.log(res.data);
                                        window.location.reload();
                                    }).catch(error => {
                                        setDeleteMsg("User not authorized to delete this group. Only group admin can delete the group.");
                                    });
                                }
                            }}>Delete group</button>
                            <button onClick={() => {
                                let name = window.prompt("Update your name:");
                                if(name !== null){
                                    Axios.put(`/api/groups/${group._id}`, {name: name}).then(res => {
                                        console.log(res.data);
                                        window.location.reload();
                                    });
                                }
                            }}>Update name</button>
                            <p style={{color: "rosybrown"}}>{delete_msg}</p>
                        </div>
                    )
                })
            )
        } else {
            return (<h3>No groups</h3>)
        }
    } 

    return (
        <>
            {DisplayGroups(props)}
        </>
    )
    
}