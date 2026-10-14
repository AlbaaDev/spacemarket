export const environment = {
    production: true,
    // baseUrl : 'http://marketspace.us-east-1.elasticbeanstalk.com'
    baseUrl: 'https://spacemarket-back-hjapg7ekdub5fzeg.canadacentral-01.azurewebsites.net',
    // The backend refuses POST /auth/register in production; the sign-up page and links are hidden too.
    registrationOpen: false,
}