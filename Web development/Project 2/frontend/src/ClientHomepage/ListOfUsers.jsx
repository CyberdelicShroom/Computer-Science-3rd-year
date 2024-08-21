import { Field } from "formik";

export default function ListOfUsers(props){

    const DisplayUsers = (props) => {
        const {users} = props;

        if(users?.length > 0){
            return (
                users.map((user, index) => {
                    // console.log(user);
                    return(
                        <div className="user" key={user._id}>
                            <label><Field type="checkbox" name="users" value={user.email} />{user.email}</label>
                        </div>
                    )
                })
            )
        } else {
            return (<h3>No users</h3>)
        }
    } 

    return (
        <>
            {DisplayUsers(props)}
        </>
    )
    
}