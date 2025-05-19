import React, { useEffect, useState } from 'react'
import { TbCircleDashed } from "react-icons/tb"
import { BiCommentDetail } from "react-icons/bi"
import { AiOutlineSearch } from "react-icons/ai"
import { ImAttachment } from "react-icons/im"
import { BsEmojiAngry, BsEmojiSmile, BsFilter, BsMicFill, BsThreeDotsVertical } from "react-icons/bs"
import ChatCard from './ChatCard/ChatCard'
import MessageCard from './MessageCard/MessageCard'
import "./HomePage.css"
import { useNavigate } from 'react-router-dom'
import Profile from './Profile/Profile'
import { Button, Menu, MenuItem } from '@mui/material'
import CreateGroup from './Group/CreateGroup'
import { useDispatch, useSelector } from 'react-redux'
import { currentUser, LogoutAction, searchUser } from '../Redux/Auth/Action'
import { createChat, getUsersChat } from '../Redux/Chat/Action'
import { createMessage, getAllMessages } from '../Redux/Message/Action'

import SockJS from 'sockjs-client/dist/sockjs'
import { over } from "stompjs"

const HomePage = () => {

  const [querys, setQuerys] = useState(null);
  const [currentChat, setCurrentChat] = useState(null);
  const [content, setContent] = useState("");
  const [isProfile, setIsProfile] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [isGroup, setIsGroup] = useState(false)
  const { auth, chat, message } = useSelector(store => store);
  const [stompClient, setStompClient] = useState();
  const [isconnect, setIsConnected] = useState(false)
  const [messages, setMessages] = useState([]);

  const open = Boolean(anchorEl);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const token = localStorage.getItem("token");

  const connect = () => {
    const sock = new SockJS("http://localhost:5454/websocket")
    const temp = over(sock);
    setStompClient(temp);

    const headers = {
      Authorization: `Bearer ${token}`,
      "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
    }

    temp.connect(headers, onConnect, onError)
  }

  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop().split(";").shift();
    }
  }

  const onError = (error) => {
    console.log("error ", error)
  }


  // const onConnect=()=>{
  //   setIsConnected(true)
  // }

  const onConnect = () => {
    setIsConnected(true);
    if (auth.reqUser && currentChat) {
      stompClient.subscribe("/group/" + currentChat.id.toString(), onMessageReceive);
    }
  };

  useEffect(() => {
    if (stompClient && isconnect && currentChat) {
      const subscription = stompClient.subscribe("/group/" + currentChat.id.toString(), onMessageReceive);
      return () => subscription.unsubscribe();
    }
  }, [currentChat]);


  useEffect(() => {
    if (message.newMessage && stompClient) {
      setMessages([...messages, message.newMessage])
      stompClient?.send("/app/message", {}, JSON.stringify(message.newMessage))
    }
  }, [message.newMessage])

  useEffect(() => {
    setMessages(message.messages)
  }, [message.messages])

  const onMessageReceive = (payload) => {
    console.log("Receive Message-------------", JSON.parse(payload.body))

    const receiveMessage = JSON.parse(payload.body)
    setMessages([...messages, receiveMessage])
  }

  useEffect(() => {
    if (isconnect && stompClient && auth.reqUser && currentChat) {
      const subscription = stompClient.subscribe("/group/" + currentChat.id.toString(), onMessageReceive);

      console.log("helloo......")
      return () => {
        subscription.unsubscribe();
      }
    }
  })

  useEffect(() => {
    connect();
  }, [])

  const handleClick = (e) => {
    setAnchorEl(e.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleSearch = (keyword) => {
    dispatch(searchUser({ keyword, token }))
  };

  const handleClickonChatCard = (userId) => {
    // setCurrentChat(true)
    dispatch(createChat({ token, data: { userId } }))
    setQuerys("")
  }

  const handleCurrentChat = (item) => {
    setCurrentChat(item)
  }

  const handleCreateNewMessage = () => {
    dispatch(createMessage({
      token,
      data: { chatId: currentChat.id, content: content },
    })
    );
    console.log("created new message")
  };

  const handleNavigate = () => {
    setIsProfile(true)
  }
  const handleCloseOpenProfile = () => {
    setIsProfile(false)
  }
  const handleCreateGroup = () => {
    setIsGroup(true)
  }
  const handleCloseCreateGroup = () => {
    setIsGroup(false);
    setAnchorEl(null);
  };
  const handleLogout = () => {
    dispatch(LogoutAction())
    navigate("/signup")
  }

  useEffect(() => {
    dispatch(getUsersChat({ token }))
  }, [chat.createdChat, chat.createdGroup])

  useEffect(() => {
    if (!auth.reqUser) {
      navigate("/signup")
    }
  }, [auth.reqUser])

  useEffect(() => {
    dispatch(currentUser(token))
  }, [token])

  useEffect(() => {
    if (currentChat?.id)
      dispatch(getAllMessages({ chatId: currentChat.id, token }))
  }, [currentChat, message.newMessage])

  return (
    <div className='relative'>
      <div className="w-full py-14 bg-[#00a884]"></div>
      <div className='flex bg-[#f0f2f5] h-[90vh] absolute left-[2vw] top-[5vh] w-[96vw]'>
        <div className='left w-[30%] bg-[#e8e9ec] h-full'>
          {/* Profile */}
          {/* {isGroup && <CreateGroup />} */}
          {isGroup && <CreateGroup setIsGroup={setIsGroup} handleCloseCreateGroup={handleCloseCreateGroup} />}

          {isProfile && <div className='h-full w-full'><Profile handleCloseOpenProfile={handleCloseOpenProfile} /> </div>}

          {!isProfile && !isGroup && <div className='w-full'>
            {/* Home */}
            {<div className='flex justify-between items-center p-3'>
              <div onClick={handleNavigate} className='flex items-center space-x-3'>
                <img
                  className='rounded-full w-10 h-10 cursor-pointer'
                  src={auth.reqUser?.profile_picture || "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_640.png"}
                  alt=""
                />
                <p className='cursor-pointer'>{auth.reqUser?.full_name}</p>
              </div>
              <div className='space-x-3 text-2xl flex'>
                <TbCircleDashed className='cursor-pointer' onClick={() => navigate("/status")} />
                <BiCommentDetail />
                <div>
                  <BsThreeDotsVertical id="demo-positioned-button"
                    aria-controls={open ? 'demo-positioned-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                    className='cursor-pointer'
                  />
                  <Menu
                    id="demo-positioned-menu"
                    aria-labelledby="demo-positioned-button"
                    anchorEl={anchorEl}
                    open={open}
                    onClose={handleClose}
                    anchorOrigin={{
                      vertical: 'top',
                      horizontal: 'left',
                    }}
                    transformOrigin={{
                      vertical: 'top',
                      horizontal: 'left',
                    }}
                  >
                    <MenuItem onClick={handleClose}>Profile</MenuItem>
                    <MenuItem onClick={handleCreateGroup}>Create Group</MenuItem>
                    <MenuItem onClick={handleLogout}>Logout</MenuItem>
                  </Menu>

                </div>
              </div>
            </div>}

            <div className='relative flex justify-center items-center bg-white py-4 px-3'>
              <input className='border-none outline-none bg-slate-200 rounded-md w-[90%] pl-9 py-2'
                type="text"
                placeholder='Search or Start New Chat'
                onChange={(e) => {
                  setQuerys(e.target.value)
                  handleSearch(e.target.value)
                }}
                value={querys}
              />
              <AiOutlineSearch className='left-5 top-7 absolute' />
              <div>
                <BsFilter className='ml-4 text-3xl' />
              </div>

            </div>

            {/* All Users list */}
            <div className='bg-white overflow-y-scroll h-[73vh] px-3'>

              {querys &&
                auth.searchUser?.map((item) => (
                  <div onClick={() => handleClickonChatCard(item.id)}>
                    {" "}
                    <hr /> <ChatCard
                      name={item.full_name}
                      userImg={item.profile_picture || "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"}
                    />
                  </div>
                ))}

              {chat.chats.length > 0 && !querys && chat.chats?.map((item) => (
                <div onClick={() => handleCurrentChat(item)}>
                  <hr /> {item.group ? (
                    <ChatCard
                      name={item.chat_name}
                      userImg={item.chat_image || "https://cdn.pixabay.com/photo/2024/09/21/00/30/group-9062674_640.png"}

                    />

                  ) : (
                    <ChatCard
                      isChat={true}
                      name={
                        auth.reqUser?.id !== item.users[0]?.id
                          ? item.users[0].full_name
                          : item.users[1].full_name
                        // "user"
                      }
                      userImg={
                        auth.reqUser?.id !== item.users[0]?.id
                          ? item.users[0].profile_picture ||
                          "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                          : item.users[1].profile_picture ||
                          "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                      }
                    />
                  )

                  }

                </div>
              ))}

            </div>
            {/* <div className='bg-white overflow-y-scroll h-[73vh] px-3'>
              {querys &&
                auth.searchUser?.map((item) => (
                  <div onClick={() => handleClickonChatCard(item.id)}>
                    {" "}
                    <hr /> <ChatCard
                      name={item.full_name}
                      userImg={
                        item.profile_picture ||
                        "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                      }
                    />
                  </div>
                ))}

              {chat.chats.length > 0 && !querys &&
                chat.chats?.map((item) => (
                  <div onClick={() => handleClickonChatCard(item.id)}>

                    <hr /> {item.is_group ? (
                      <ChatCard
                        name={item.chat_name}
                        userImg={item.chat_image ||
                          "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                        }
                      />
                    ) : (
                      <ChatCard
                        isChat={true}
                        name={
                          auth.reqUser?.id !== item.users[0]?.id
                            ? item.users[0].full_name
                            : item.users[1].full_name
                        }
                        userImg={
                          auth.reqUser.id !== item.users[0].id
                            ? item.users[0].profile_picture ||
                            "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                            : item.users[1].profile_picture ||
                            "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                        }
                        />
                    )}
                  </div>
                ))}

            </div> */}


          </div>
          }
        </div>

        {/* Default ChatsApp page  */}
        {!currentChat && <div className='w-[70%] flex flex-col items-center justify-center h-full' >
          <div className='max-w-[70%] text-center'>
            <img src="https://assets.skyfilabs.com/images/blog/web-based-chat-application-with-webcam-using-php.webp" alt="" />
            <h1 className='text-4xl text-gray-600'>ChatsApp Web</h1>
            <p className='my-9'>Send and Receive messages without keeping your phone online. Use ChatsApp on up to 4 Linked devices and 1 phone at same time.</p>
          </div>
        </div>}



        {/* Message Part */}
        {currentChat && <div className='w-[70%] relative bg-blue-200'>

          <div className='header absolute top-0 w-full bg-[#f0f3f5]'>
            <div className='flex justify-between'>
              <div className='py-3 space-x-4 flex items-center px-3'>
                <img className="w-10 h-10 rounded-full"
                  src={currentChat.group ? currentChat.chat_image : (auth.reqUser?.id !== currentChat.users[0].id    //I added ? 1 time here
                    ? currentChat.users[0].profile_picture ||
                    "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png"
                    : currentChat.users[1].profile_picture ||
                    "https://cdn.pixabay.com/photo/2023/02/18/11/00/icon-7797704_1280.png")} />
                <p>
                  {currentChat.group ? currentChat.chat_name : (auth.reqUser?.id === currentChat.users[0].id ? currentChat.users[1].full_name : currentChat.users[0].full_name)}
                </p>
              </div>
              <div className='py-3 flex space-x-4 items-center px-3'>
                <AiOutlineSearch />
                <BsThreeDotsVertical />
              </div>
            </div>
          </div>

          {/* Message Section */}
          <div className='px-10 h-[85vh] overflow-y-scroll'>
            <div className='space-y-1 flex flex-col justify-center mt-20 py-2'>

              {messages.length > 0 && messages?.map((item, i) => (
                <MessageCard
                  isReqUserMessage={item.user.id !== auth.reqUser.id}
                  content={item.content}
                />
              ))}


            </div>
          </div>
          {/* Footer Part */}
          <div className='footer bg-[#f0f2f5] absolute bottom-0 w-full py-3 text-2xl'>
            <div className='flex justify-between items-center px-5 relative'>

              <BsEmojiSmile className='cursor-pointer' />
              <ImAttachment />

              <input className='py-2 outline-none border-none bg-white pl-4 rounded-md w-[85%]'
                type="text"
                onChange={(e) => setContent(e.target.value)}
                placeholder='Type Message'
                value={content}
                onKeyPress={(e) => {
                  if (e.key === "Enter") {
                    handleCreateNewMessage();
                    setContent("")
                  }
                }}
              />
              <BsMicFill />

            </div>
          </div>





        </div>}
      </div>
    </div>
  )
}

export default HomePage