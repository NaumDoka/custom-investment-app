export interface menu {
  header?: string;
  title?: string;
  icon?: any;
  to?: string;
  chip?: string;
  chipBgColor?: string;
  chipColor?: string;
  chipVariant?: string;
  chipIcon?: string;
  children?: menu[];
  disabled?: boolean;
  type?: string;
  subCaption?: string;
  isPro?: boolean
}

const sidebarItem: menu[] = [
  { header: "Home" },
  {
    title: "Home",
    icon: "home-smile-linear",
    to: "/Home",
    isPro: false,
  },


  { header: "PAGES" },
  {
    title: "Tables",
    icon: "tablet-linear",
    to: "#",

    children: [
      {
        title: 'Administration Table',
        to: '/shadcn-table/administration-table',
        isPro: false,
      },

    ]

  },
  {
    title: 'User Profile',
    icon: 'user-circle-linear',
    to: '/user-profile',
    isPro: false,

  },

  { header: 'Auth' },
  {
    title: 'Login',
    icon: 'login-2-linear',
    to: '#',

    children: [
      {
        title: 'Boxed Login',
        to: '/auth/login2',
        isPro: false,
      }
    ]
  },
  {
    title: 'Register',
    icon: 'user-plus-rounded-linear',
    to: '#',
    isPro: true,
    children: [
      {
        title: 'Boxed Register',
        to: '/auth/register2',
        isPro: false,
      }
    ]
  }
];

export default sidebarItem;