interface Language {
    [key: string]: {
      sideBar: {
        home: string;
        allEvents: string;
        registeredEvents: string;
        userInfo: string;
        adminPanel: string;
        createEvent: string;
        logout: string;
        login: string;
        signUp: string;
      };
      searchBar: {
        categories: string;
        title: string;
        date: string;
        place: string;
        search: string;
      };
      signUp: {
        username: string;
        firstName: string;
        lastName: string;
        password: string;
        email: string;
        confirmPassword: string;
        birthDate: string;
        profilePicture: string;
        home: string;
        signin: string;
        register: string;
      };
      signin: {
        username: string;
        password: string;
        home: string;
        login: string;
        register: string;
      };
      userInfo: {
        username: string;
        firstName: string;
        lastName: string;
        email: string;
        oldPassword: string;
        password: string;
        confirmPassword: string;
        submitPassword: string;
        submitInfo: string;
        errors: string[];
      };
      card: {
        categories: string;
        date: string;
        time: string;
        description: string;
        fullInfo: string;
        location: string;
        eventOwner: string;
        registeredUser: string;
        delete: string;
        modify: string;
        subscribe: string;
        unsubscribe: string;
        gMaps: string;
        title: string;
        picture: string;
        submit: string;
      };
      tagPage: {
        createTag: string;
        createTags: string;
        modifyOrDelete: string;
        addTag: string;
        addTags: string;
      };
    };
  }
export default Language;