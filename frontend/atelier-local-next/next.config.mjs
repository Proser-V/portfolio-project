/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'd1gmao6ee1284v.cloudfront.net',
                pathname: '/**',
            },
            {
                protocol: 'https',
                hostname: 'atelierlocal-bucket1.s3.eu-west-3.amazonaws.com',
                pathname: '/**',
            },
        ],
        minimumCacheTTL: 3600,
        qualities: [75, 85],
    },
};

export default nextConfig;
