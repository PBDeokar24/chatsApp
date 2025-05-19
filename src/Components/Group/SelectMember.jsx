import React from 'react'
import { AiOutlineClose } from 'react-icons/ai'

const SelectMember = ({ handleRemoveMamber, member }) => {
    return (
        <div className='flex items-center bg-slate-300 rounded-full'>
            <img className='w-7 h-7 rounded-full'
                src={member.profile_picture || "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_640.png"} alt="" />
            <p className='px-2'>{member.full_name}</p>
            <AiOutlineClose className='pr-1 cursor-pointer' onClick={handleRemoveMamber} />
        </div>
    )
}

export default SelectMember