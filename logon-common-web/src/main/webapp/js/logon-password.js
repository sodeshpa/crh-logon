function initPasswordFields() {
	var fields = document.querySelectorAll('input[type=password]:enabled');
	// for each field
	fields.forEach(function (inputElement) {
		// check if the eye/toggle is already present
		if (!inputElement.matches('.togglePassword')) {
			// add class for css selector (we can't rely on the password type)
			inputElement.classList.add('togglePassword');

			var inputWrapper = document.createElement('span');
			inputWrapper.className = 'togglePasswordParent';
			inputElement.parentNode.insertBefore(inputWrapper, inputElement);
			inputWrapper.appendChild(inputElement);

			// build and append the eye icon
			var eyeElement = document.createElement('div');
			eyeElement.className = 'eye';
			// configure click listener
			eyeElement.addEventListener('click', function (event) {
				event.stopPropagation();
				if (inputElement.type === 'password') {
					// make the password visible
					inputElement.type = 'text';
					eyeElement.className = 'eye visible';
				} else {
					// hide the password
					inputElement.type = 'password';
					eyeElement.className = 'eye';
				}
			});
			// append element
			inputWrapper.appendChild(eyeElement);
		}
	});
}

Ext.onReady(initPasswordFields);