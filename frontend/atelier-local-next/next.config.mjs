/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        domains: [
            'd1gmao6ee1284v.cloudfront.net',
            'atelierlocal-bucket1.s3.eu-west-3.amazonaws.com'
        ],
        minimumCacheTTL: 604800
    },
};

export default nextConfig;
