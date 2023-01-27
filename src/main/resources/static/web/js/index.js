const { createApp } = Vue;



createApp({
	data() {
		return {
			firstName: '',
			lastName: '',
			email: '',
			password: ''
		}
	},
	created() {

		console.log('hola')
	},
	methods: {
		login() {
			if (!this.email || !this.password) {
				Swal.fire('Please fill in all inputs', ':C', 'error')
			} else if (!this.email.includes('@') || !this.email.includes('.')) {
				Swal.fire('Please use a valid email', ':C', 'error')
			} else {
				axios.post('/api/login', `email=${this.email}&password=${this.password}`)
					.then(response => window.location.href = "http://localhost:8080/web/accounts.html")
					.catch(err => {
						let error = err.response.data.message;
						console.log(error);
						if(error == "User doesn't exist") {
							Swal.fire('Unknown user', ':C', 'error')
						} else if(error == "Wrong credentials") {
							Swal.fire('Wrong credentials', ':C', 'error')
						} else {
							Swal.fire('Please activate your account with the link sent to your email', ':C', 'error')
						}

					})
			}
		},
		signup() {
			console.log(this.password)
			if (!this.firstName || !this.lastName || !this.email || !this.password) {
				Swal.fire('Please fill in all inputs', ':C', 'error')
			} else if (!this.email.includes('@') || !this.email.includes('.')) {
				Swal.fire('Please use a valid email', ':C', 'error')
			} else {
				axios.post('/api/clients', `firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`)
					.then(response => window.location.href = "http://localhost:8080/web/process_register.html")
					.catch(err => Swal.fire('User already registered', ':C', 'error'))
			}
		}
	}
}).mount('#app')






const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const signUpButtonSm = document.getElementById('signupbtn-sm');
const signInButtonSm = document.getElementById('signinbtn-sm');
const container = document.getElementById('container');
const signInContainer = document.getElementsByClassName('sign-in-container-sm');
const signUpContainer = document.getElementsByClassName('sign-up-container-sm');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

signUpButtonSm.addEventListener('click', (e) => {
	e.preventDefault();
	signInContainer[0].classList.add('d-none');
	signUpContainer[0].classList.remove('d-none');
	signUpContainer[0].classList.add('d-flex')
});

signInButtonSm.addEventListener('click', (e) => {
	e.preventDefault();
	signUpContainer[0].classList.add('d-none');
	signInContainer[0].classList.remove('d-none');
	signInContainer[0].classList.add('d-flex')
});

