/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'd1gmao6ee1284v.cloudfront.net',
            },
            {
                protocol: 'https',
                hostname: 'atelierlocal-bucket1.s3.eu-west-3.amazonaws.com',
            },
        ],
        minimumCacheTTL: 3600,
        qualities: [90, 100],
    },
};

export default nextConfig;
