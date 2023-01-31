const { createApp } = Vue;

let url = window.location.search;
let myurl = new URLSearchParams(url)
let finalUrl = myurl.get("token")


createApp({
    data() {
        return {
            password: '',
            confirmPassword: ''
        }
    },
    created() {

    },
    methods: {
        resetPassword() {
            if (this.password != this.confirmPassword) {
                console.log('passwords do not match')
            } else {
                axios.post('/api/reset-password', `token=${finalUrl}&password=${this.password}`)
                    .then(res => {
                        
                    })
                    .catch(err => console.log(err))
            }
        }
    }
}).mount('#app')