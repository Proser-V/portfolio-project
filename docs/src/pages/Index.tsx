import { LandingHeader } from "@/components/LandingHeader";
import { HeroSection } from "@/components/HeroSection";
import { FeatureSection } from "@/components/FeatureSection";
import { AboutSection } from "@/components/AboutSection";
import { LandingFooter } from "@/components/LandingFooter";

const Index = () => {
  return (
    <div className="min-h-screen">
      <LandingHeader />
      <main>
        <HeroSection />
        <FeatureSection />
        <AboutSection />
      </main>
      <LandingFooter />
    </div>
  );
};

export default Index;
