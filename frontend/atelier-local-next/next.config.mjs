/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        domains: [
            'd1gmao6ee1284v.cloudfront.net',
            'atelierlocal-bucket1.s3.eu-west-3.amazonaws.com'
        ],
        minimumCacheTTL: 3600,
        qualities: [90, 100],
    },
};

export default nextConfig;
