class UserService {
  constructor() {
    this.token = sessionStorage.getItem("token");
    this.userId = sessionStorage.getItem("userId");
    this.userRole = sessionStorage.getItem("userRole");
    this.apiurl = "localhost:8080";
  }

  getToken() {
    return this.token;
  }

  getUserId() {
    return this.userId;
  }

  getUserRole() {
    return this.userRole;
  }

  getApiUrl() {
    return this.apiurl;
  }

  setToken(token) {
    this.token = token;
    sessionStorage.setItem("token", token);
  }

  setUserId(userId) {
    this.userId = userId;
    sessionStorage.setItem("userId", userId);
  }

  setUserRole(userRole) {
    this.userRole = userRole;
    sessionStorage.setItem("userRole", userRole);
  }

  parseJwt(token) {
    var base64Url = token.split(".")[1];
    var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    var jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  }
  determineContentType(data) {
    if (typeof data === "string") {
      return "application/x-www-form-urlencoded; charset=UTF-8";
    } else if (data instanceof FormData) {
      return false; // Let jQuery set the content type to multipart/form-data
    } else if (typeof data === "object") {
      return "application/json; charset=UTF-8";
    }
    return "application/x-www-form-urlencoded; charset=UTF-8";
  }

  async sendAjax(url, type, data) {
    try {
      const contentType = this.determineContentType(data);
      const processData = !(data instanceof FormData); // Disable processData for FormData

      return new Promise((resolve, reject) => {
        $.ajax({
          url: url,
          method: type,
          data:
            contentType === "application/json; charset=UTF-8"
              ? JSON.stringify(data)
              : data,
          contentType: contentType,
          processData: processData,
          crossDomain: true,
          xhrFields: { withCredentials: true }, // Ensures cookies are included for all AJAX calls
          headers: { Authorization: `Bearer ${this.token}` },
          success: (response) => resolve(response),
          error: (error) => reject(error),
        });
      });
    } catch (error) {
      console.error("Error setting up AJAX request:", error);
      throw error;
    }
  }
  async refreshToken() {
    try {
      return await this.sendAjax(
        `http://${this.apiurl}/api/authentication/refreshToken`,
        "GET",
        null
      );
    } catch (error) {
      console.error("Error sending refresh token request:", error);
      throw error;
    }
  }
  async authenticate() {
    try {
      const authResponse = await this.sendAjax(
        `http://${this.apiurl}/api/authentication/isAuthenticated`,
        "GET",
        null
      );
      if (authResponse.status === "OK") {
        return await this.handleAuthResponse(authResponse.data);
      } else {
        return { authenticated: false };
      }
    } catch (authResponse) {
      return this.handleAuthError(authResponse);
    }
  }

  async handleAuthResponse(authData) {
    if (authData.at === "Valid" && authData.rt === "Valid") {
      return { authenticated: true };
    } else if (authData.at !== "Valid" && authData.rt === "Valid") {
      try {
        const refreshTokenResponse = await this.refreshToken();
        if (refreshTokenResponse && refreshTokenResponse.status === "OK") {
          const { sub, role } = this.parseJwt(refreshTokenResponse.data.at);
          this.setUserId(sub);
          this.setUserRole(role);
          this.setToken(refreshTokenResponse.data.at);
          return { authenticated: true };
        } else {
          return { authenticated: false };
        }
      } catch (error) {
        console.error("Error during token refresh:", error);
        return { authenticated: false };
      }
    } else if (authData.at === "Valid") {
      return { authenticated: true };
    }
    return { authenticated: false };
  }

  handleAuthError(authResponse) {
    if (
      authResponse.responseJSON &&
      authResponse.responseJSON.status === "FORBIDDEN"
    ) {
      return { authenticated: false };
    } else {
      console.error("Error during authentication:", authResponse);
      return { authenticated: false };
    }
  }

  async sendAjaxWithAuthen(url, type, data) {
    try {
      const result = await this.sendAjax(url, type, data);
      return result;
    } catch (error) {
      if (
        error.responseJSON &&
        (error.responseJSON.status === "FORBIDDEN" ||
          error.responseJSON.status === "UNAUTHORIZED")
      ) {
        const authen = await this.authenticate();
        if (authen.authenticated) {
          try {
            return await this.sendAjax(url, type, data);
          } catch (retryError) {
            console.error(
              "Error during retry after authentication:",
              retryError
            );
            throw retryError;
          }
        } else {
          console.error("Authentication failed:", authen);
          throw new Error("Authentication failed");
        }
      } else {
        console.error("Error during AJAX request:", error);
        throw error;
      }
    }
  }
}
export default UserService;

// determineContentType(data) {
//   if (typeof data === "string") {
//     return "application/x-www-form-urlencoded; charset=UTF-8";
//   } else if (data instanceof FormData) {
//     return false; // Let jQuery set the content type to multipart/form-data
//   } else if (typeof data === "object") {
//     return "application/json; charset=UTF-8";
//   }
//   return "application/x-www-form-urlencoded; charset=UTF-8";
// }

// async sendAjax(url, type, data) {
//   try {
//     const contentType = this.determineContentType(data);
//     const processData = !(data instanceof FormData); // Disable processData for FormData

//     return await new Promise((resolve, reject) => {
//       $.ajax({
//         url: url,
//         method: type,
//         data: contentType === "application/json; charset=UTF-8" ? JSON.stringify(data) : data,
//         contentType: contentType,
//         processData: processData,
//         xhrFields: {
//           withCredentials: true, // Ensures cookies are included for all AJAX calls
//         },
//         headers: {
//           Authorization: `Bearer ${this.token}`,
//         },
//         success: resolve,
//         error: reject,
//       });
//     });
//   } catch (error) {
//     console.error("Error setting up AJAX request:", error);
//     throw error; // Re-throw the error to be handled by the calling function
//   }
// }

// async authenticate() {
//   try {
//     const authResponse = await this.sendAjax(`http://${this.apiurl}/api/authentication/isAuthenticated`, "GET");

//     console.log("authResponse:", authResponse); // Log the authResponse

//     if (!authResponse) {
//       console.error("authResponse is undefined or null");
//       return { authenticated: false };
//     }

//     if (authResponse.responseJSON && authResponse.responseJSON.status === "FORBIDDEN") {
//       return { authenticated: false };
//     }

//     if (authResponse.status === "OK") {
//       const authData = authResponse.data;
//       if (authData.at === "Valid" && authData.rt === "Valid") {
//         return { authenticated: true };
//       }

//       if (authData.at !== "Valid" && authData.rt === "Valid") {
//         const refreshTokenResponse = await this.sendAjax(`http://${this.apiurl}/api/authentication/refreshToken`, "GET");

//         console.log("refreshTokenResponse:", refreshTokenResponse); // Log the refreshTokenResponse

//         if (refreshTokenResponse && refreshTokenResponse.status === "OK") {
//           this.setToken(refreshTokenResponse.data.at);
//           return { authenticated: true };
//         }
//       } else if (authData.at === "Valid") {
//         return { authenticated: true };
//       }
//     }

//     return { authenticated: false };
//   } catch (error) {
//     console.error("Error in authenticate:", error);
//     return { authenticated: false }; // Return false if there is an error during authentication
//   }
// }

// async sendAjaxWithAuthen(url, type, data) {
//   try {
//     const authResult = await this.authenticate();
//     if (!authResult.authenticated) {
//       throw new Error("User is not authenticated");
//     }
//     return await this.sendAjax(url, type, data);
//   } catch (error) {
//     console.error("Error in sendAjaxWithAuthen:", error);
//     throw error; // Re-throw the error to be handled by the calling function
//   }
// }
