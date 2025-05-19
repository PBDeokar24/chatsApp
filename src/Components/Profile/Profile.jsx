import React, { useState } from 'react'
import { BsArrowLeft, BsCheck2, BsPencil } from 'react-icons/bs'
import { useNavigate } from 'react-router-dom'
import { updateUser } from '../../Redux/Auth/Action';
import { useDispatch, useSelector } from 'react-redux';

const Profile = ({ handleCloseOpenProfile }) => {
    const navigate = useNavigate();
    const [flag, setFlag] = useState(false);
    const [username, setUsername] = useState(null);
    const [tempPicture, setTempPicture] = useState(null);
    const { auth } = useSelector(store => store)
    const dispatch = useDispatch()

    const handleFlag = () => {
        setFlag(true);
    }
    const handleCheckClick = () => {
        setFlag(false);
        const data = {
            id: auth.reqUser?.id,
            token: localStorage.getItem("token"),
            data: { full_name: username},
        };
        dispatch(updateUser(data))
    }
    const handleNameChange = (e) => {
        setUsername(e.target.value);
        console.log(username)
    }
    const handleUpdateName = (e) =>{
        const data = {
            id: auth.reqUser?.id,
            token: localStorage.getItem("token"),
            data: { full_name: username},
        };
        if(e.target.key==="Enter")
                dispatch(updateUser(data))
    }

    const uploadToCloudinary = (pics) => {
        const data = new FormData();
        data.append("file", pics);
        data.append("upload_preset", "chatsApp")
        data.append("cloud_name", "dfsl5pkoh")
        fetch("https://api.cloudinary.com/v1_1/dfsl5pkoh/image/upload", {
            method: "post",
            body: data,
        })
            .then((res) => res.json())
            .then((data) => {
                console.log("imgurl", data)
                setTempPicture(data.url.toString())

                const dataa = {
                    id: auth.reqUser.id,
                    token: localStorage.getItem("token"),
                    data: { profile_picture: data.url.toString() }
                };
                dispatch(updateUser(dataa))
            })

    }


    return (
        <div className='h-full w-full'>
            <div className='flex items-center space-x-5 bg-[#008069] text-white px-5 pb-5 pt-5'>
                <BsArrowLeft className='cursor-pointer text-2xl font-bold' onClick={handleCloseOpenProfile} />
                <p className='font-bold'>Profile</p>
            </div>

            {/* Update profile pic section */}
            <div className='flex flex-col justify-center items-center my-12'>
                <label htmlFor="imgInput">
                    <img className='rounded-full w-[15vw] h-[15vw] cursor-pointer'
                        src={auth.reqUser?.profile_picture || tempPicture || "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_640.png"} alt="" />
                </label>

                <input onChange={(e) => uploadToCloudinary(e.target.files[0])} type="file" id='imgInput' className='hidden' />
            </div>

            {/* Name section */}
            <div className='bg-white px-3'>
                <p className='py-3'>Your Name</p>

                {!flag && <div className='w-full flex justify-between items-center'>
                    <p className='py-3'>{auth.reqUser.full_name || "UserName"}</p>
                    <BsPencil onClick={handleFlag} className='cursor-pointer' />
                </div>}

                {flag && <div className='w-full flex justify-between items-center py-2'>
                    <input 
                        onKeyPress={handleUpdateName} 
                        onChange={handleNameChange}
                        className='w=[80%] outline-none border-b-2 border-blue-700 p-2' 
                        type="text" p
                        laceholder='Enter Your Name'
                         />

                    <BsCheck2 
                    onClick={handleCheckClick} 
                    className='cursor-pointer text-2xl ' 
                    />

                </div>}
            </div>



        </div>

    )
}

export default Profile