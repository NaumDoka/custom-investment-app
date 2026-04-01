// project imports

import type {
  notificationType,
  profileType,
} from "../types/HeaderTypes";
//
// Notification
//


const notifications: notificationType[] = [
  {

    title: "Roman Joined the Team!",
    subtitle: "Congratulate him",
  },
  {

    title: "New message received",
    subtitle: "Salma sent you new message",
  },
  {

    title: "New Payment received",
    subtitle: "Check your earnings",
  },
  {

    title: "Jolly completed tasks",
    subtitle: "Assign her new tasks",
  },
  {

    title: "Roman Joined the Team!",
    subtitle: "$230 deducted from account",
  },
];


//
// Profile
//



const profileDD: profileType[] = [
  {
    title: "My Profile",
    subtitle: "Account Settings",
    url: "/user-profile",
    img: "tabler:user",
  },
  {
    title: "Admin Actions",
    subtitle: "My Admin Actions",
    url: "/hidden-admin-9823",
    img: "tabler:mail",
  },
];




export {
  notifications,
  profileDD,



};
