# What is this

A really simple self-hostable mail sending service.

Configure it to connect to an existing account on an SMTP server, then send emails with a single API Route.

## Who it is for?

For really small projects, or simple university projects that requires outgoing email.

> **NOTE:** The sent messages are not stored!
 
#### Javascript Example:

```javascript
const url = 'http://localhost:44321/send';
const headers = new Headers({
  'Authorization': '<SNIP>'
});

const formData = new FormData();
formData.append('content', 'If you see this message, it\'s working. But I might be in the junk too!');
formData.append('subject', 'Hello World!');
formData.append('target', 'yeo_dev@protonmail.com');

fetch(url, {
  method: 'POST',
  headers: headers,
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))

{"success":true,"message":"Email sent successfully."}
```

## Deploying

Check the [wiki](https://github.com/wqyeo/Just-Send-The-Mail/wiki/Deploying).
