import React from 'react';

interface CareSyncLogoProps {
  size?: number | string;
  className?: string;
  use4kImage?: boolean;
}

export const CareSyncLogo: React.FC<CareSyncLogoProps> = ({
  size = 32,
  className = '',
  use4kImage = false,
}) => {
  if (use4kImage) {
    return (
      <img
        src="/logo-4k.png"
        alt="CareSync Logo 4K"
        className={`object-contain rounded-xl select-none ${className}`}
        style={{ width: size, height: size }}
      />
    );
  }

  return (
    <svg
      viewBox="0 0 1024 1024"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={`select-none shrink-0 ${className}`}
      style={{ width: size, height: size }}
    >
      {/* Squircle Teal Background */}
      <rect x="20" y="20" width="984" height="984" rx="225" fill="#0E9F8E" />
      
      {/* White Medical Cross */}
      <rect x="432" y="205" width="160" height="614" rx="80" fill="#FFFFFF" />
      <rect x="205" y="432" width="614" height="160" rx="80" fill="#FFFFFF" />
      
      {/* Saffron / Clinical Orange Accent Dot */}
      <circle cx="708" cy="273" r="95" fill="#ED751E" />
    </svg>
  );
};
