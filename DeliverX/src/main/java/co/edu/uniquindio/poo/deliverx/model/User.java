package co.edu.uniquindio.poo.deliverx.model;

public class User {
    private String userId;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;


    protected User(Builder<?> builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.password = builder.password;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
    }


    public static class Builder<T extends Builder<T>> {
        protected String userId;
        protected String name;
        protected String password;
        protected String email;
        protected String phoneNumber;

        public T userId(String userId) {
            this.userId = userId;
            return self();
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T password(String password) {
            this.password = password;
            return self();
        }

        public T email(String email) {
            this.email = email;
            return self();
        }

        public T phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return self();
        }

        protected T self() {
            // Cast to the specific type of Builder
            return (T) this;
        }

        public User build() {
            return new User(this);
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return
                "userId=" + userId + '\t' +
                ", name=" + name + '\t' +
                ", password=" + password + '\t' +
                ", email=" + email + '\t' +
                ", phoneNumber=" + phoneNumber + '\t';
    }
}
